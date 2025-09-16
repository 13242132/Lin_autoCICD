package com.example.ai.codegeneration.constant;

/**
 * 项目常量类
 */
public class ProjectConstants {

    /**
     * Maven POM文件内容
     */
    public static final String MAVEN_POM_CONTENT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            " <project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "     xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
            "     <modelVersion>4.0.0</modelVersion>\n" +
            "     <parent>\n" +
            "         <groupId>org.springframework.boot</groupId>\n" +
            "         <artifactId>spring-boot-starter-parent</artifactId>\n" +
            "         <version>3.5.3</version>\n" +
            "         <relativePath/> <!-- lookup parent from repository -->\n" +
            "     </parent>\n" +
            "     <groupId>com.example</groupId>\n" +
            "     <artifactId>disk-mode-test</artifactId>\n" +
            "     <version>0.0.1-SNAPSHOT</version>\n" +
            "     <name>disk-mode-test</name>\n" +
            "     <description>Auto-generated Spring Boot project with H2 support</description>\n" +
            "     <properties>\n" +
            "         <java.version>21</java.version>\n" +
            "         <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n" +
            "         <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>\n" +
            "         <maven.compiler.encoding>UTF-8</maven.compiler.encoding>\n" +
            "     </properties>\n" +
            "     <dependencies>\n" +
        
            "        <!-- JWT 支持（Token 生成与解析） -->\n" +
            "        <dependency>\n" +
            "            <groupId>io.jsonwebtoken</groupId>\n" +
            "            <artifactId>jjwt-api</artifactId>\n" +
            "            <version>0.11.5</version>\n" +
            "        </dependency>\n" +
            "        <dependency>\n" +
            "            <groupId>io.jsonwebtoken</groupId>\n" +
            "            <artifactId>jjwt-impl</artifactId>\n" +
            "            <version>0.11.5</version>\n" +
            "            <scope>runtime</scope>\n" +
            "        </dependency>\n" +
            "        <dependency>\n" +
            "            <groupId>io.jsonwebtoken</groupId>\n" +
            "            <artifactId>jjwt-jackson</artifactId>\n" +
            "            <version>0.11.5</version>\n" +
            "            <scope>runtime</scope>\n" +
            "        </dependency>\n" +
        
            "         <dependency>\n" +
            "             <groupId>org.springframework.boot</groupId>\n" +
            "             <artifactId>spring-boot-starter-web</artifactId>\n" +
            "         </dependency>\n" +
            "         <!-- Spring Boot Validation -->\n" +
            "         <dependency>\n" +
            "             <groupId>org.springframework.boot</groupId>\n" +
            "             <artifactId>spring-boot-starter-validation</artifactId>\n" +
            "         </dependency>\n" +
            "         <!-- Jackson for JsonFormat annotation -->\n" +
            "         <dependency>\n" +
            "             <groupId>com.fasterxml.jackson.core</groupId>\n" +
            "             <artifactId>jackson-annotations</artifactId>\n" +
            "         </dependency>\n" +
            "         <dependency>\n" +
            "             <groupId>org.springframework.boot</groupId>\n" +
            "             <artifactId>spring-boot-starter-data-jpa</artifactId>\n" +
            "         </dependency>\n" +
            "         <dependency>\n" +
            "             <groupId>com.h2database</groupId>\n" +
            "             <artifactId>h2</artifactId>\n" +
            "             <scope>runtime</scope>\n" +
            "         </dependency>\n" +
            "         <dependency>\n" +
            "             <groupId>org.flywaydb</groupId>\n" +
            "             <artifactId>flyway-core</artifactId>\n" +
            "         </dependency>\n" +
            "         <dependency>\n" +
            "             <groupId>org.projectlombok</groupId>\n" +
            "             <artifactId>lombok</artifactId>\n" +
            "             <optional>true</optional>\n" +
            "         </dependency>\n" +
            "         <dependency>\n" +
            "             <groupId>org.springframework.boot</groupId>\n" +
            "             <artifactId>spring-boot-starter-test</artifactId>\n" +
            "             <scope>test</scope>\n" +
            "         </dependency>\n" +
            "     </dependencies>\n" +
            "  \n" +
            "     <build>\n" +
            "         <plugins>\n" +
            "             <plugin>\n" +
            "                 <groupId>org.springframework.boot</groupId>\n" +
            "                 <artifactId>spring-boot-maven-plugin</artifactId>\n" +
            "             </plugin>\n" +
            "         </plugins>\n" +
            "     </build>\n" +
            "  \n" +
            " </project>";

    /**
     * H2数据库配置文件内容
     */
    public static final String H2_CONFIG_CONTENT = "# H2 Database Configuration (File-based for persistence)\n" +
            "spring.datasource.url=jdbc:h2:file:./data/testdb;AUTO_SERVER=TRUE\n" +
            "spring.datasource.driverClassName=org.h2.Driver\n" +
            "spring.datasource.username=sa\n" +
            "spring.datasource.password=password\n" +
            "\n" +
            "# H2 Console (for development)\n" +
            "spring.h2.console.enabled=true\n" +
            "spring.h2.console.path=/h2-console\n" +
            "\n" +
            "# Disable Spring SQL auto init (we use Flyway instead)\n" +
            "spring.sql.init.mode=never\n" +
            "\n" +
            "# JPA Configuration\n" +
            "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect\n" +
            "spring.jpa.hibernate.ddl-auto=none\n" +
            "spring.jpa.show-sql=true\n" +
            "spring.jpa.properties.hibernate.format_sql=true\n" +
            "\n" +
            "# Flyway Configuration\n" +
            "spring.flyway.enabled=true\n" +
            "spring.flyway.locations=classpath:db/migration\n" +
            "spring.flyway.baseline-on-migrate=true\n" +
            "spring.flyway.validate-on-migrate=false\n" +
            "\n" +
            "# Server Configuration\n" +
            "server.port=8090\n" +
            "\n" +
            "# File upload configuration\n" +
            "spring.servlet.multipart.max-file-size=50MB\n" +
            "spring.servlet.multipart.max-request-size=50MB\n" +
            "\n" +
            "# Logging Configuration\n" +
            "logging.level.com.example.demo=DEBUG\n" +
            "logging.level.org.springframework.web=DEBUG\n" +
            "\n" +
            "# JWT Configuration\n" +
            "jwt.secret=MySuperSecretKeyForJWTTokenGenerationThatIsMuchLongerAndMoreSecure123456789\n" +
            "jwt.expiration=86400000\n" +
            "jwt.header=Authorization\n" +
            "jwt.prefix=Bearer\n";


    /**
     * 应用启动类内容
     */
    public static final String APPLICATION_ENTRY_CLASS_CONTENT = "package com.example.demo;\n\n" +
            "import org.springframework.boot.SpringApplication;\n" +
            "import org.springframework.boot.autoconfigure.SpringBootApplication;\n\n" +
            "@SpringBootApplication\n" +
            "public class {0}Application {\n\n" +
            "    public static void main(String[] args) {\n" +
            "        SpringApplication.run({0}Application.class, args);\n" +
            "    }\n\n" +
            "}";
}