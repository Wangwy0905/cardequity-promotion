package com.youyu.cardequity.promotion.biz;


import com.youyu.common.generator.ConfigurationBuilder;
import com.youyu.common.generator.model.YyGeneratorConfig;
import com.youyu.common.generator.model.YyTableConfig;
import io.micrometer.core.instrument.config.InvalidConfigurationException;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.config.Configuration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationBuilderMySqlTest {

    public static void main(String[] args) throws InterruptedException, SQLException, InvalidConfigurationException, IOException, org.mybatis.generator.exception.InvalidConfigurationException {
        /**
         * 初始化 db oracleDB.sql
         */
        oracle();
    }

    public static void oracle() throws InvalidConfigurationException, InterruptedException, SQLException, IOException, org.mybatis.generator.exception.InvalidConfigurationException {
//        Generator.startDB();
        YyGeneratorConfig config = new YyGeneratorConfig();
        config.setTargetProject("/分期商城/cardequity-promotion/cardequity-promotion-biz/src/main/java");
        config.setBasePackage("com.youyu.cardequity.promotion.biz");
        config.setAppId("cardequity-promotion");
        config.setGenerateMapperXml(true);
        JDBCConnectionConfiguration connectionConfiguration = new JDBCConnectionConfiguration();
        connectionConfiguration.setDriverClass("com.mysql.jdbc.Driver");
        connectionConfiguration.setConnectionURL("jdbc:mysql://192.168.1.193:3306/cardequity_promotion?useUnicode=true&amp;characterEncoding=UTF-8");
        connectionConfiguration.setUserId("cardequity_promotion");
        connectionConfiguration.setPassword("cardequity_promotion");
        config.setJdbcConnectionConfiguration(connectionConfiguration);

        YyTableConfig t1 = new YyTableConfig();
        t1.setTableName("TB_%");
//        t1.setPkColumn("id");
        t1.setSqlStatement("JDBC");
//        t1.setSqlStatement("select SEQ_{1}.nextval from dual");
//        ColumnOverride columnOverride = new ColumnOverride("STATUS");
//        columnOverride.setJavaType("com.youyu.common.enums.WslStatus");
//        t1.addColumnOverride(columnOverride);
        config.addTableConfig(t1);


//        YyTableConfig t2 = new YyTableConfig();
//        t2.setTableName("WSL_STUDENT");
//        t2.setPkColumn("stu_id");
//        t2.setSqlStatement("select SEQ_WSL_STUDENT.nextval from dual");
//        ColumnOverride columnOverride = new ColumnOverride("STATUS");
//        columnOverride.setJavaType("com.youyu.common.enums.WslStatus");
//        t2.addColumnOverride(columnOverride);
//        config.addTableConfig(t2);

        Configuration configuration = ConfigurationBuilder.build(config);

        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
//        ConfigurationParser cp = new ConfigurationParser(warnings);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(configuration, callback, warnings);
        myBatisGenerator.generate(null);
        for (String warning : warnings) {
            System.out.println(warning);
        }
    }
}
