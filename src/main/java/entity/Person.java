package entity;

import entity.datatypes.Sex;

public class Person implements Comparable<Person> {

    private final String name;
    private final String surname;
    private final String birthDate;
    private final Sex sex;
    private final String city;
    private final String id;
    private final String domain;


    public Person(String name, String surname, String birthDate, Sex sex, String city, String id, String domain) {
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.sex = sex;
        this.city = city;
        this.id = id;
        this.domain = domain;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public Sex getSex() {
        return sex;
    }

    public String getCity() {
        return city;
    }

    public String getId() {
        return id;
    }

    public String getDomain() {
        return domain;
    }

    @Override
    public int compareTo(Person o) {
        if (this.name.compareTo(o.name) == 0)
            return this.surname.compareTo(o.surname);

        return this.name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", sex=" + sex +
                ", city='" + city + '\'' +
                ", id='" + id + '\'' +
                ", domain='" + domain + '\'' +
                '}';
    }

    public String toCSV() {
        return this.name + ";" +
                this.surname + ";" +
                this.birthDate + ";" +
                this.sex + ";" +
                this.city + ";" +
                this.id + ";" +
                this.domain + ";\n";
    }
}
