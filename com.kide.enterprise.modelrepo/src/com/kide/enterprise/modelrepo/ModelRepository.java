package com.kide.enterprise.modelrepo;

import java.util.List;
import java.util.Optional;

public interface ModelRepository {
    Optional<ModelSnapshot> read(ModelPath path);

    List<ModelPath> list();

    ModelTransaction beginTransaction();
}
