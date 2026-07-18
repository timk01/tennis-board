package tennisboard.entity;

public class PlayerEntity {
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

/*
ID	Int	Первичный ключ, автоинкремент
Name	Varchar	Имя игрока
 */