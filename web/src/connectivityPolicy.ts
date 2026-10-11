/** Read-only authenticated connectivity probe configuration.
 * Fail before network requests unless two explicitly approved HTTPS staging
 * hosts, a known project, and a dedicated Firebase principal are provided.
 * Never log or serialize the returned credentials.
 */
export interface ConnectivitySettings {
  api: string;
  web: string;
  projectId: string;
  apiKey: string;
  email: string;
  password: string;
}

export function readConnectivitySettings(
  env: Record<string, string | undefined>
): ConnectivitySettings {
  const requireValue = (name: string): string => {
    const value = env[name]?.trim();
    if (!value) throw new Error("Missing required connectivity setting: " + name);
    return value;
  };
  const approvedApi = requireValue("KIDE_MONITOR_APPROVED_API_HOST");
  const approvedWeb = requireValue("KIDE_MONITOR_APPROVED_WEB_HOST");

  const checkOrigin = (name: string, approved: string): string => {
    const raw = requireValue(name);
    let url: URL;
    try {
      url = new URL(raw);
    } catch {
      throw new Error(name + " must be a credential-free HTTPS service origin");
    }
    if (url.protocol !== "https:" ||
        url.username || url.password || url.search || url.hash ||
        url.pathname !== "/" || url.hostname !== approved ||
        url.hostname === "localhost" || url.hostname === "127.0.0.1" ||
        url.hostname === "::1") {
      throw new Error(name + " must match its approved HTTPS staging hostname");
    }
    return url.origin;
  };

  const api = checkOrigin("KIDE_MONITOR_API_ORIGIN", approvedApi);
  const web = checkOrigin("KIDE_MONITOR_WEB_ORIGIN", approvedWeb);
  if (api === web) throw new Error("Web and API must be independently deployed services");

  const projectId = requireValue("KIDE_MONITOR_EXPECTED_PROJECT_ID");
  if (!/^kide:project:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/.test(projectId)) {
    throw new Error("Expected project ID must be a canonical KIDE project identity");
  }

  return {
    api, web, projectId,
    apiKey: requireValue("KIDE_MONITOR_FIREBASE_API_KEY"),
    email: requireValue("KIDE_MONITOR_ENGINEER_EMAIL"),
    password: requireValue("KIDE_MONITOR_ENGINEER_PASSWORD")
  };
}
