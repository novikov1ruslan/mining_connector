package org.miner.conector;

public class Ping {
    public String id;

//    public Ping(String id) {
//        this.id = id;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ping ping = (Ping) o;

        return id != null ? id.equals(ping.id) : ping.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Ping{" +
                "id='" + id + '\'' +
                '}';
    }
}
