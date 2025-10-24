-- Kiểm tra các bảng đã được tạo trong database
-- Sử dụng lệnh: docker exec sqlserver /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "SaPassword123!" -C -d db_quanlysinhvien -i check_tables.sql

USE db_quanlysinhvien;
GO

-- Liệt kê tất cả các bảng
SELECT TABLE_NAME
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_TYPE = 'BASE TABLE'
ORDER BY TABLE_NAME;
GO

-- Đếm số dòng trong mỗi bảng
SELECT
    t.NAME AS TableName,
    p.rows AS RowCounts
FROM
    sys.tables t
INNER JOIN
    sys.partitions p ON t.object_id = p.OBJECT_ID
WHERE
    t.is_ms_shipped = 0
    AND p.index_id IN (0,1)
GROUP BY
    t.Name, p.Rows
ORDER BY
    t.Name;
GO
