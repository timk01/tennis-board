package tennisboard.reposiory;

import org.springframework.stereotype.Repository;

@Repository
public class HelloRepository {

    public String helloTestRepo() {
        return "hello, here";
    }
}
