package com.kide.enterprise.modelrepo;

import java.util.Optional;

public interface ModelRepository {
    Optional<ModelSnapshot> read(ModelPath path);

    ModelTransaction beginTransaction();
}
