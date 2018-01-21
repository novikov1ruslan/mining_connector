package org.miner.conector;

public class Ping {
    private String email;
    private String id;

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ping ping = (Ping) o;

        if (email != null ? !email.equals(ping.email) : ping.email != null) return false;
        return id != null ? id.equals(ping.id) : ping.id == null;
    }

    @Override
    public int hashCode() {
        int result = email != null ? email.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Ping{" +
                "email='" + email + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
