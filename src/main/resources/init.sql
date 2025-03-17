PRINT 'Starting database creation...';
GO

IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'character_service_db')
BEGIN
    CREATE DATABASE character_service_db;
    PRINT 'Database character_service_db created successfully';
END
ELSE
BEGIN
    PRINT 'Database character_service_db already exists';
END
GO 