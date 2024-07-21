package com.probendi.itgraph;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

@ApplicationScoped
public class DataSourceProducer {

    @Produces
    @ApplicationScoped
    public DataSource createDataSource() {
        String url = System.getenv("POSTGRES_URL");
        if (url == null || url.isBlank()) {
            url = "jdbc:postgresql://localhost:5432/it-graph?user=postgres";
        }
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL(url);
        return dataSource;
    }
}
