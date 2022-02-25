TRUNCATE `tb_record`;
UPDATE `tb_product` SET stock=1000,version_id=1 WHERE id=1;

SELECT COUNT(*) FROM tb_record; 