CREATE TABLE USUARIOS(
  --
  nombre        VARCHAR(50),
  contrasenia   VARCHAR(10),
  --
  CONSTRAINT PK_USUARIOS PRIMARY KEY (nombre)
  --
);

INSERT INTO USUARIOS (nombre, contrasenia) VALUES ('jhon', 'password1');