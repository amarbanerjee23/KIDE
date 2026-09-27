#!/usr/bin/env python3
from __future__ import annotations

import argparse
import getpass
import json
import os
from pathlib import Path
import shlex
import subprocess
import sys
import time
from typing import Any
from urllib.error import HTTPError, URLError
from urllib.parse import urlencode
from urllib.request import Request, urlopen


FIREBASE_API = "https://firebase.googleapis.com/v1beta1"
IDENTITY_ADMIN_API = "https://identitytoolkit.googleapis.com/admin/v2"
IDENTITY_API = "https://identitytoolkit.googleapis.com/v1"


class BootstrapError(RuntimeError):
    pass


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description=(
            "Provision Firebase Authentication for KIDE and write the "
            "Cloud Build values needed by the hosted deployment."
        )
    )
    parser.add_argument("--project-id", required=True)
    parser.add_argument("--output", required=True, type=Path)
    parser.add_argument(
        "--web-app-display-name",
        default=os.environ.get("KIDE_FIREBASE_WEB_APP_NAME", "KIDE Web"),
    )
    return parser.parse_args()


def run(*args: str) -> str:
    try:
        completed = subprocess.run(
            list(args),
            check=True,
            capture_output=True,
            text=True,
        )
    except FileNotFoundError as exc:
        raise BootstrapError(f"Required command is not installed: {args[0]}") from exc
    except subprocess.CalledProcessError as exc:
        detail = (exc.stderr or exc.stdout or "").strip()
        raise BootstrapError(
            f"Command failed: {' '.join(args)}"
            + (f"\n{detail}" if detail else "")
        ) from exc
    return completed.stdout.strip()


def ensure_apis(project_id: str) -> None:
    services = (
        "firebase.googleapis.com",
        "identitytoolkit.googleapis.com",
    )
    try:
        run(
            "gcloud",
            "services",
            "enable",
            *services,
            "--project",
            project_id,
        )
    except BootstrapError as exc:
        raise BootstrapError(
            "Firebase setup requires permission to enable "
            "firebase.googleapis.com and identitytoolkit.googleapis.com. "
            "Run this bootstrap with a project administrator that has "
            "serviceusage.services.enable, or enable those APIs once in the "
            "Google Cloud console before retrying.\n"
            + str(exc)
        ) from exc


def access_token(project_id: str) -> str:
    token = run("gcloud", "auth", "print-access-token", "--project", project_id)
    if not token:
        raise BootstrapError("gcloud returned an empty access token.")
    return token


def request_json(
    method: str,
    url: str,
    *,
    token: str | None = None,
    project_id: str | None = None,
    payload: dict[str, Any] | None = None,
    allow_404: bool = False,
) -> Any:
    body = None
    headers = {"Accept": "application/json", "User-Agent": "kide-firebase-bootstrap"}
    if token:
        headers["Authorization"] = f"Bearer {token}"
    if project_id:
        headers["X-Goog-User-Project"] = project_id
    if payload is not None:
        body = json.dumps(payload).encode("utf-8")
        headers["Content-Type"] = "application/json"

    req = Request(url=url, method=method, data=body, headers=headers)
    try:
        with urlopen(req, timeout=120) as response:
            raw = response.read()
            return json.loads(raw.decode("utf-8")) if raw else {}
    except HTTPError as exc:
        raw = exc.read().decode("utf-8", errors="replace")
        if allow_404 and exc.code == 404:
            return None
        try:
            parsed = json.loads(raw) if raw else {}
        except json.JSONDecodeError:
            parsed = {}
        message = (
            parsed.get("error", {}).get("message")
            if isinstance(parsed, dict)
            else None
        )
        raise BootstrapError(
            f"{method} {url} failed with HTTP {exc.code}: "
            f"{message or raw or exc.reason}"
        ) from exc
    except URLError as exc:
        raise BootstrapError(f"{method} {url} failed: {exc}") from exc


def wait_operation(name: str, token: str, project_id: str) -> dict[str, Any]:
    url = f"{FIREBASE_API}/{name}"
    deadline = time.monotonic() + 300
    while time.monotonic() < deadline:
        operation = request_json(
            "GET",
            url,
            token=token,
            project_id=project_id,
        )
        if operation.get("done"):
            error = operation.get("error")
            if error:
                raise BootstrapError(
                    f"Firebase operation {name} failed: "
                    f"{error.get('message', json.dumps(error))}"
                )
            response = operation.get("response")
            if not isinstance(response, dict):
                raise BootstrapError(
                    f"Firebase operation {name} completed without a response."
                )
            return response
        time.sleep(2)
    raise BootstrapError(f"Timed out waiting for Firebase operation {name}.")


def ensure_firebase_project(project_id: str, token: str) -> None:
    project_url = f"{FIREBASE_API}/projects/{project_id}"
    current = request_json(
        "GET",
        project_url,
        token=token,
        project_id=project_id,
        allow_404=True,
    )
    if current is not None:
        print(f"Firebase is already enabled for {project_id}.")
        return

    operation = request_json(
        "POST",
        f"{FIREBASE_API}/projects/{project_id}:addFirebase",
        token=token,
        project_id=project_id,
        payload={},
    )
    name = operation.get("name")
    if not name:
        raise BootstrapError("Firebase addFirebase did not return an operation name.")
    wait_operation(name, token, project_id)
    print(f"Enabled Firebase for {project_id}.")


def ensure_web_app(
    project_id: str,
    token: str,
    display_name: str,
) -> dict[str, Any]:
    apps_url = f"{FIREBASE_API}/projects/{project_id}/webApps?pageSize=100"
    listed = request_json(
        "GET",
        apps_url,
        token=token,
        project_id=project_id,
    )
    apps = listed.get("apps", []) if isinstance(listed, dict) else []
    app = next(
        (
            item
            for item in apps
            if isinstance(item, dict) and item.get("displayName") == display_name
        ),
        None,
    )
    if app is None and len(apps) == 1 and isinstance(apps[0], dict):
        app = apps[0]
        print(
            "Reusing the project's existing Firebase Web App "
            f"{app.get('displayName') or app.get('appId')}."
        )

    if app is None:
        operation = request_json(
            "POST",
            f"{FIREBASE_API}/projects/{project_id}/webApps",
            token=token,
            project_id=project_id,
            payload={"displayName": display_name},
        )
        name = operation.get("name")
        if not name:
            raise BootstrapError(
                "Firebase Web App creation did not return an operation name."
            )
        app = wait_operation(name, token, project_id)
        print(f"Created Firebase Web App {display_name}.")
    else:
        print(f"Using Firebase Web App {app.get('displayName') or display_name}.")

    resource_name = app.get("name")
    if not resource_name:
        raise BootstrapError("Firebase Web App does not contain its resource name.")

    config = request_json(
        "GET",
        f"{FIREBASE_API}/{resource_name}/config",
        token=token,
        project_id=project_id,
    )
    api_key = config.get("apiKey")
    if not isinstance(api_key, str) or not api_key.strip():
        raise BootstrapError("Firebase Web App config does not contain an API key.")
    return config


def ensure_auth_configuration(project_id: str, token: str) -> None:
    config_url = f"{IDENTITY_ADMIN_API}/projects/{project_id}/config"
    current = request_json(
        "GET",
        config_url,
        token=token,
        project_id=project_id,
        allow_404=True,
    )
    if current is not None:
        print("Firebase Authentication configuration is already initialized.")
        return

    initialize_url = (
        "https://identitytoolkit.googleapis.com/v2/"
        f"projects/{project_id}/identityPlatform:initializeAuth"
    )
    try:
        request_json(
            "POST",
            initialize_url,
            token=token,
            project_id=project_id,
            payload={},
        )
        print("Initialized Firebase Authentication configuration.")
    except BootstrapError as exc:
        # Another actor may have initialized Authentication after our GET.
        # Treat an already-exists race as success, but surface all other errors.
        if "HTTP 409" not in str(exc):
            raise

    deadline = time.monotonic() + 120
    while time.monotonic() < deadline:
        current = request_json(
            "GET",
            config_url,
            token=token,
            project_id=project_id,
            allow_404=True,
        )
        if current is not None:
            return
        time.sleep(2)

    raise BootstrapError(
        "Firebase Authentication configuration did not become available "
        "after initializeAuth."
    )


def enable_email_password(project_id: str, token: str) -> None:
    query = urlencode({"updateMask": "signIn.email"})
    url = f"{IDENTITY_ADMIN_API}/projects/{project_id}/config?{query}"
    request_json(
        "PATCH",
        url,
        token=token,
        project_id=project_id,
        payload={
            "signIn": {
                "email": {
                    "enabled": True,
                    "passwordRequired": True,
                }
            }
        },
    )
    print("Firebase email/password authentication is enabled.")


def password_from_operator() -> str:
    file_name = os.environ.get("KIDE_FIREBASE_ADMIN_PASSWORD_FILE", "").strip()
    if file_name:
        path = Path(file_name)
        if not path.is_file():
            raise BootstrapError(
                f"KIDE_FIREBASE_ADMIN_PASSWORD_FILE does not exist: {path}"
            )
        password = path.read_text(encoding="utf-8").rstrip("\r\n")
    else:
        if not sys.stdin.isatty():
            raise BootstrapError(
                "KIDE_FIREBASE_ADMIN_PASSWORD_FILE is required in "
                "non-interactive mode when KIDE_FIREBASE_ADMIN_UID is unset."
            )
        password = getpass.getpass("Firebase administrator password (hidden): ")

    if len(password) < 6:
        raise BootstrapError(
            "Firebase administrator password must be at least 6 characters."
        )
    return password


def admin_email_from_operator() -> str:
    email = os.environ.get("KIDE_FIREBASE_ADMIN_EMAIL", "").strip()
    if not email:
        if not sys.stdin.isatty():
            raise BootstrapError(
                "KIDE_FIREBASE_ADMIN_EMAIL is required in non-interactive mode "
                "when KIDE_FIREBASE_ADMIN_UID is unset."
            )
        email = input("Firebase administrator email: ").strip()
    if not email or "@" not in email:
        raise BootstrapError("Firebase administrator email is invalid.")
    return email


def public_auth_request(
    action: str,
    api_key: str,
    payload: dict[str, Any],
) -> tuple[dict[str, Any] | None, str | None]:
    url = f"{IDENTITY_API}/accounts:{action}?{urlencode({'key': api_key})}"
    try:
        result = request_json("POST", url, payload=payload)
        if not isinstance(result, dict):
            raise BootstrapError(f"Firebase {action} returned an invalid response.")
        return result, None
    except BootstrapError as exc:
        text = str(exc)
        marker = ": "
        provider_message = text.rsplit(marker, 1)[-1] if marker in text else text
        return None, provider_message


def ensure_admin_uid(api_key: str) -> str:
    configured_uid = os.environ.get("KIDE_FIREBASE_ADMIN_UID", "").strip()
    if configured_uid:
        print("Using the configured Firebase administrator UID.")
        return configured_uid

    email = admin_email_from_operator()
    password = password_from_operator()
    try:
        signed_in, sign_in_error = public_auth_request(
            "signInWithPassword",
            api_key,
            {
                "email": email,
                "password": password,
                "returnSecureToken": True,
            },
        )
        if signed_in is not None:
            uid = signed_in.get("localId")
            if isinstance(uid, str) and uid:
                print(f"Signed in existing Firebase administrator {email}.")
                return uid
            raise BootstrapError(
                "Firebase sign-in succeeded without returning an administrator UID."
            )

        created, create_error = public_auth_request(
            "signUp",
            api_key,
            {
                "email": email,
                "password": password,
                "returnSecureToken": True,
            },
        )
        if created is not None:
            uid = created.get("localId")
            if isinstance(uid, str) and uid:
                print(f"Created Firebase administrator {email}.")
                return uid
            raise BootstrapError(
                "Firebase sign-up succeeded without returning an administrator UID."
            )

        if create_error and "EMAIL_EXISTS" in create_error:
            raise BootstrapError(
                "The Firebase administrator account already exists but the "
                "supplied password did not sign it in. Retry with the correct "
                "password or set KIDE_FIREBASE_ADMIN_UID explicitly."
            )
        raise BootstrapError(
            "Firebase administrator could not be signed in or created. "
            f"Sign-in result: {sign_in_error or 'unknown error'}; "
            f"sign-up result: {create_error or 'unknown error'}."
        )
    finally:
        password = "\0" * len(password)


def write_output(
    path: Path,
    project_id: str,
    api_key: str,
    admin_uid: str,
) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    content = (
        f"KIDE_FIREBASE_PROJECT_ID={shlex.quote(project_id)}\n"
        f"KIDE_FIREBASE_API_KEY={shlex.quote(api_key)}\n"
        f"KIDE_FIREBASE_ADMIN_UID={shlex.quote(admin_uid)}\n"
    )
    path.write_text(content, encoding="utf-8")
    path.chmod(0o600)


def main() -> int:
    args = parse_args()
    project_id = args.project_id.strip()
    if not project_id:
        raise BootstrapError("--project-id cannot be empty.")

    print(f"Configuring Firebase Authentication for {project_id}...")
    ensure_apis(project_id)
    token = access_token(project_id)
    ensure_firebase_project(project_id, token)
    config = ensure_web_app(project_id, token, args.web_app_display_name)
    ensure_auth_configuration(project_id, token)
    enable_email_password(project_id, token)

    api_key = str(config["apiKey"]).strip()
    admin_uid = ensure_admin_uid(api_key)
    write_output(args.output, project_id, api_key, admin_uid)

    print("Firebase Authentication bootstrap complete.")
    print(f"Firebase project: {project_id}")
    print(f"Initial administrator UID: {admin_uid}")
    print("Firebase Web API key was resolved automatically from the Web App config.")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except BootstrapError as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        raise SystemExit(2)
