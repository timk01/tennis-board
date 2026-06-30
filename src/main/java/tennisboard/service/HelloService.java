package tennisboard.service;

import org.springframework.stereotype.Service;
import tennisboard.reposiory.HelloRepository;

@Service
public class HelloService {

    private final HelloRepository repository;

    public HelloService(HelloRepository repository) {
        this.repository = repository;
    }

    public String helloFromService() {
        return repository.helloTestRepo();
    }
}
