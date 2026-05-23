#!/usr/bin/env python3
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
import json
from pathlib import Path
from urllib.parse import urlparse
from kide_product.services.transformer import transform_activity_to_mnc

ROOT = Path(__file__).parent

class ApiHandler(BaseHTTPRequestHandler):
    def _send(self, code, body, ctype="application/json"):
        if isinstance(body, (dict, list)):
            body = json.dumps(body, indent=2).encode()
        elif isinstance(body, str):
            body = body.encode()
        self.send_response(code)
        self.send_header("Content-Type", ctype)
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)

    def do_GET(self):
        path = urlparse(self.path).path
        if path in ["/", "/index.html"]:
            self._send(200, (ROOT/"templates"/"index.html").read_text(), "text/html; charset=utf-8")
            return
        if path == "/api/health":
            self._send(200, {"status":"ok","service":"kide-product"})
            return
        if path.startswith("/static/"):
            p = ROOT / path.lstrip("/")
            if p.exists() and p.is_file():
                ctype = "application/javascript" if p.suffix == ".js" else "text/plain"
                self._send(200, p.read_bytes(), ctype)
                return
        self._send(404, {"error": "Not found"})

    def do_POST(self):
        path = urlparse(self.path).path
        if path != "/api/transform":
            self._send(404, {"error":"Not found"})
            return
        try:
            size = int(self.headers.get("Content-Length", "0"))
            payload = json.loads(self.rfile.read(size).decode("utf-8"))
            self._send(200, transform_activity_to_mnc(payload))
        except Exception as exc:
            self._send(400, {"error": str(exc)})

if __name__ == "__main__":
    server = ThreadingHTTPServer(("0.0.0.0", 8080), ApiHandler)
    print("KIDE Product Webapp running on http://127.0.0.1:8080")
    server.serve_forever()
