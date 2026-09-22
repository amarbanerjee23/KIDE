knowledge BrokenKrl {
  namespace bad = "ftp://not-allowed";

  template JsonBody(name: string) for json
    body "{\"name\":\"${name}\"}";

  target Escape type java {
    template JsonBody;
    output "../Escape.java";
    bind name: string = string "Escape";
  }
}
