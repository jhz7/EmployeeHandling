CREATE TABLE empleados (
  --
  nmempleado       int4,
  cdempleado       varchar(15) NOT NULL,
  dsnombre         varchar(120) NOT NULL,
  feregistro       varchar(10) NOT NULL,
  snactivo         varchar(1) NOT NULL,
  --
  CONSTRAINT pk_empleados PRIMARY KEY (nmempleado),
  CONSTRAINT ck_empleados CHECK (snactivo IN ('S', 'N'))
  --
);
