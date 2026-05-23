async function init(){
  document.getElementById('load').onclick=async()=>{
    const res=await fetch('/static/example.json');
    document.getElementById('in').value=await res.text();
  }
  document.getElementById('run').onclick=async()=>{
    const res=await fetch('/api/transform',{method:'POST',headers:{'Content-Type':'application/json'},body:document.getElementById('in').value});
    document.getElementById('out').value=await res.text();
  }
}
init();
