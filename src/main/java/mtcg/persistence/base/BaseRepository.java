package mtcg.persistence.base;

import com.google.common.base.CaseFormat;
import http.model.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mtcg.model.entity.annotation.Column;
import mtcg.model.entity.annotation.IgnoreUpdate;
import mtcg.model.entity.annotation.Table;
import org.postgresql.jdbc.PgArray;
import org.postgresql.util.PSQLException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Slf4j
public abstract class BaseRepository<T> {

    private final ConnectionPool connectionPool;
    private final Class<T> type;

    public List<T> getEntitiesById(Long id) {
        return getEntitiesByFilter("id", id);
    }

    public Optional<T> getEntityById(Long id) {
        return getEntityByFilter("id", id);
    }

    public List<T> getEntitiesByFilter(Object... args) {
        return select(args);
    }

    public Optional<T> getEntityByFilter(Object... args) {
        return select(args).stream().findFirst();
    }

    @SneakyThrows
    public Long insert(T entity) {
        List<Field> fields = Arrays.stream(type.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Column.class))
                .filter(field -> !field.getName().equals("id"))
                .collect(Collectors.toList());
        String values = IntStream.range(0, fields.size())
                .mapToObj(i -> "?")
                .collect(Collectors.joining(","));
        String insertColumns = fields.stream()
                .map(field -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName()))
                .collect(Collectors.joining(","));

        String query = String.format("insert into %s (%s) values (%s)", getTableName(), insertColumns, values);
        log.debug("Sending query: {}", query);
        Connection connection = connectionPool.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < fields.size(); i++) {
                Object input = type.getMethod("get" + fields.get(i).getName().toUpperCase().charAt(0) + fields.get(i).getName().substring(1)).invoke(entity);
                if (input instanceof Object[]) {
                    input = connection.createArrayOf("BIGINT", (Object[]) input);
                }
                preparedStatement.setObject(i + 1, input);
            }
            try {
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows == 0) {
                    log.error("Could not insert row! query: {}", query);
                    throw new IllegalStateException();
                }
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getLong(1);
                    } else {
                        log.error("Insert failed! No ID obtained with query {}", query);
                        throw new IllegalStateException();
                    }
                }
            } catch (PSQLException e) {
                e.printStackTrace();
                throw new BadRequestException("Choose an other username!"); // TODO create new unique constraint exception
            }
        }
    }

    @SneakyThrows
    public boolean update(T entity) {
        List<Field> fields = Arrays.stream(type.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Column.class))
                .filter(field -> !field.isAnnotationPresent(IgnoreUpdate.class))
                .filter(field -> !field.getName().equals("id"))
                .collect(Collectors.toList());
        String updateColumns = fields.stream()
                .map(field -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName()) + " = ?")
                .collect(Collectors.joining(","));

        String query = String.format("update %s set %s where id = ?", getTableName(), updateColumns);
        log.debug("Sending query: {}", query);
        Connection connection = connectionPool.getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        for (int i = 0; i < fields.size(); i++) {
            Object input = type.getMethod("get" + fields.get(i).getName().toUpperCase().charAt(0) + fields.get(i).getName().substring(1)).invoke(entity);
            if (input instanceof Object[]) {
                input = connection.createArrayOf("BIGINT", (Object[]) input);
            }
            preparedStatement.setObject(i + 1, input);
        }
        preparedStatement.setLong(fields.size() + 1, (Long) type.getMethod("getId").invoke(entity));
        try {
            preparedStatement.execute();
        } catch (PSQLException e) {
            throw new BadRequestException("Choose an other username!");// TODO create new unique constraint exception
        }
        return true;
    }

    @SneakyThrows
    public boolean update(Long id, Object... params) {
        if (params.length % 2 != 0) {
            throw new IllegalStateException();
        }
        Map<String, Object> filter = new HashMap<>();
        for (int i = 1; i < params.length; i += 2) {
            filter.put((String) params[i - 1], params[i]);
        }

        String updateColumn = filter.keySet().stream()
                .map(key -> key + " = ?")
                .collect(Collectors.joining(", "));

        String query = String.format("update %s set %s where id = ?", getTableName(), updateColumn);
        log.debug("Sending query: {}", query);
        Connection connection = connectionPool.getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        int counter = 1;
        for (Object value : filter.values()) {
            preparedStatement.setObject(counter++, value);
        }
        preparedStatement.setObject(counter, id);
        try {
            preparedStatement.execute();
        } catch (PSQLException e) {
            throw new BadRequestException("Choose an other username!");// TODO create new unique constraint exception
        }
        return true;
    }

    @SneakyThrows
    public boolean delete(Long id) {
        String query = String.format("delete from %s where id = ?", getTableName());
        log.debug("Sending query: {}", query);
        Connection connection = connectionPool.getConnection();

        // TODO close prepared statements
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setLong(1, id);
        try {
            preparedStatement.execute();
        } catch (PSQLException e) {
            throw new BadRequestException("Choose an other username!");// TODO create new unique constraint exception
        }
        return true;
    }

    private String getTableName() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Table annotation = type.getAnnotation(Table.class);
        String tableName = (String) annotation.annotationType().getDeclaredMethod("value").invoke(annotation, (Object[]) null);
        return String.format("\"%s\"", tableName);
    }

    @SneakyThrows
    private List<T> select(Object... args) {
        if (args.length % 2 != 0) {
            throw new IllegalStateException();
        }
        Map<String, Object> filter = new HashMap<>();
        for (int i = 1; i < args.length; i += 2) {
            filter.put((String) args[i - 1], args[i]);
        }
        String selectColumns = Arrays.stream(type.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Column.class))
                .map(field -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName()))
                .collect(Collectors.joining(","));

        String query = String.format("select %s from %s", selectColumns, getTableName());
        if (!filter.isEmpty()) {
            String whereClause = " where " + filter.keySet().stream()
                    .map(this::whereKey)
                    .collect(Collectors.joining(" and "));
            query += whereClause;
        }
        log.debug("Sending query: {}", query);
        Connection connection = connectionPool.getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        for (int i = 0; i < filter.size(); i++) {
            Object input = filter.values().toArray()[i];
            if (input instanceof Collection) {
                input = connection.createArrayOf("BIGINT", ((Collection<?>) input).toArray());
            }
            preparedStatement.setObject(i + 1, input);
        }
        ResultSet rs = preparedStatement.executeQuery();
        List<T> result = new ArrayList<>();
        while (rs.next()) {
            result.add(convertToEntity(rs));
        }
        return result;
    }

    @SneakyThrows
    private T convertToEntity(ResultSet rs) {
        Class<?>[] types = Arrays.stream(type.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Column.class))
                .map(Field::getType)
                .toArray(Class[]::new);

        Object[] values = IntStream.range(1, types.length + 1)
                .mapToObj(i -> checkAndConvert(getResultSetObject(i, rs), types[i - 1]))
                .toArray();
        return type.getConstructor(types).newInstance(values);
    }

    @SneakyThrows
    private Object getResultSetObject(int index, ResultSet rs) {
        return rs.getObject(index);
    }

    @SneakyThrows
    private Object checkAndConvert(Object value, Class<?> type) {
        if (value == null) {
            return value;
        }
        if (value instanceof PgArray) {
            return ((PgArray) value).getArray();
        } else if (value instanceof Timestamp) {
            return ((Timestamp) value).toLocalDateTime();
        } else if (Enum.class.isAssignableFrom(type)) {
            return Enum.valueOf((Class<Enum>) type, (String) value);
        }
        return value;
    }

    private String whereKey(String key) {
        if (key.contains("in")) {
            return key.replace("in", "= any (?)");
        } else if (key.contains(" ")) {
            return key + " ?";
        } else {
            return key + " = ?";
        }
    }
}
