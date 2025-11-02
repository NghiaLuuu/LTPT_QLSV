-- Migration: add refresh_token column to users table
IF NOT EXISTS (SELECT * FROM sys.columns WHERE Name = N'refresh_token' AND Object_ID = Object_ID(N'users'))
BEGIN
    ALTER TABLE users ADD refresh_token NVARCHAR(255) NULL;
END
GO

