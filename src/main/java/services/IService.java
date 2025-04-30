//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package services;

import java.sql.SQLException;
import java.util.List;

public interface IService<T> {
    List<T> readList() throws SQLException;

    void add(T var1) throws SQLException;

    void update(T var1) throws SQLException;

    void addP(T var1) throws SQLException;

    void delete(int var1) throws SQLException;
}
