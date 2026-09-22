#!/usr/bin/env python3
from __future__ import annotations
import argparse, os, subprocess, sys, tarfile, tempfile
from pathlib import Path

class SmokeError(RuntimeError): pass

def find_linux_archive(products: Path) -> Path:
    matches=sorted(p for p in products.iterdir() if p.is_file() and "linux.gtk.x86_64" in p.name and p.name.endswith(".tar.gz"))
    if len(matches)!=1: raise SmokeError(f"expected one Linux desktop archive, found {len(matches)}")
    return matches[0]

def safe_extract(archive: Path, destination: Path) -> None:
    root=destination.resolve()
    with tarfile.open(archive,"r:gz") as tar:
        for member in tar.getmembers():
            target=(destination/member.name).resolve()
            if target!=root and root not in target.parents: raise SmokeError(f"unsafe archive member: {member.name}")
        tar.extractall(destination)

def find_launcher(root: Path) -> Path:
    candidates=[]
    for path in root.rglob("kide"):
        if path.is_file() and not ({"plugins","jre","jdk"} & {part.lower() for part in path.parts}):
            candidates.append(path)
    if not candidates: raise SmokeError("packaged Linux KIDE launcher was not found")
    return sorted(candidates,key=lambda p:(len(p.parts),str(p)))[0]

def main() -> int:
    parser=argparse.ArgumentParser()
    parser.add_argument("--products",required=True,type=Path)
    args=parser.parse_args()
    archive=find_linux_archive(args.products)
    with tempfile.TemporaryDirectory(prefix="kide-pr34-runtime-") as td:
        root=Path(td); safe_extract(archive,root)
        launcher=find_launcher(root); launcher.chmod(launcher.stat().st_mode|0o111)
        workspace=root/"workspace"; workspace.mkdir()
        result=subprocess.run([str(launcher),"-nosplash","-consoleLog","-application","com.kide.knowledge.selfcheck","-data",str(workspace)],
            cwd=launcher.parent,env=os.environ.copy(),text=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT,timeout=90,check=False)
        output=result.stdout or ""
        markers=[
            "KIDE PR34 KNOWLEDGE FABRIC SELF-CHECK OK",
            "KIDE PR35 KNOWLEDGE CATALOGUE TRACE SELF-CHECK OK",
        ]
        missing=[marker for marker in markers if marker not in output]
        if result.returncode!=0 or missing:
            raise SmokeError(
                f"packaged PR35 knowledge fabric/catalogue self-check failed "
                f"({result.returncode}, missing={missing})\n{output[-6000:]}")
        print(f"PR35 PACKAGED KNOWLEDGE FABRIC + CATALOGUE QUALIFIED: {archive.name}")
    return 0

if __name__=="__main__":
    try: raise SystemExit(main())
    except (SmokeError,subprocess.TimeoutExpired) as exc:
        print(f"ERROR: {exc}",file=sys.stderr); raise SystemExit(2)
