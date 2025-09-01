package backend.futurefinder.model.user;

import lombok.Value;

@Value(staticConstructor = "of")
public class UserId {
    String id;
}