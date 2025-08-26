# LibraryApp

A Java-based library management application that allows you to manage books in a PostgreSQL database.

## Features

- List all books in the database
- Import books from TSV (Tab-Separated Values) files
- Configuration management using properties files
- Interactive command-line interface

## Setup

### 1. Database Setup

1. Install PostgreSQL and create a database named `localdb`
2. Run the database setup script:
   ```sql
   psql -d localdb -f database_setup.sql
   ```

### 2. Configuration

1. Copy the configuration template:
   ```bash
   cp config.properties.template config.properties
   ```

2. Edit `config.properties` with your database credentials:
   ```properties
   DB_URL=jdbc:postgresql://localhost:5432/localdb
   DB_USER=your_username
   DB_PASSWORD=your_password
   ```

**Note:** The `config.properties` file is excluded from version control to protect sensitive information.

### 3. Build and Run

```bash
# Build the application
./gradlew build

# Run the application
./gradlew run
```

## Usage

### Command Line Arguments

```bash
# List all books
./gradlew run --args="listbooks"

# Import books from TSV file
./gradlew run --args="importbooks data/books.tsv"

# Show help
./gradlew run --args="help"
```

### Interactive Mode

Run the application without arguments to enter interactive mode:

```bash
./gradlew run
```

Available commands:
- `help` - Show available commands
- `listbooks` - List all books in the database
- `importbooks <FILE_PATH>` - Import books from a TSV file
- `quit` - Exit the application

## TSV File Format

The import functionality expects TSV files with the following format:

```
id	isbn	title	author	publication_year
1	978-3-8362-9544-4	Java ist auch eine Insel	Christian Ullenboom	2023
2	978-3-658-43573-8	Grundkurs Java	Dietmar Abts	2024
```

**Note:** The first line should be a header row and will be skipped during import.

## Testing

```bash
# Run all tests
./gradlew test

# Compile only
./gradlew compileJava
```

## Security

- Database credentials are stored in `config.properties` (not in version control)
- The application uses prepared statements to prevent SQL injection
- Configuration template is provided for easy setup

## Dependencies

- Java 22
- PostgreSQL JDBC Driver (42.7.3)
- JUnit Jupiter (for testing)


