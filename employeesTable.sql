CREATE TABLE empleados (
  --
  nmempleado       int4,
  cdempleado       varchar(15),
  dsnombre        varchar(120),
  feregistro      date,
  snactivo        varchar(1),
  --
  CONSTRAINT pk_empleados PRIMARY KEY (nmempleado)
  --
);

insert into empleados (nmempleado, cdempleado, dsnombre, feregistro, snactivo)
  values(1, '1127064277', 'JHON ZAMBRANO', '2018-07-03', 'S');

insert into empleados (nmempleado, cdempleado, dsnombre, feregistro, snactivo)
  values(2, '1127352583', 'YIRLENIA MALDONADO', '2018-07-03', 'S');