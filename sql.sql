Create table Cliente(
DNI varchar(50) primary key,
Nombre varchar(50),
Apellido varchar(50),
Edad integer,
Email varchar(50),
contraseña varchar(250)
);

Create table CuentaBancaria(
IBAN integer primary key AUTO_INCREMENT,
dinero double,
DNI_cliente varchar(50),
foreign key (DNI_cliente) references cliente(DNI) on delete cascade
);

Create table Transferencia(
IBAN_ORIGEN integer,
IBAN_DESTINO integer,
dinero double,
fecha varchar(50),
foreign key (IBAN_ORIGEN) references cuentabancaria(IBAN) on delete cascade,
foreign key (IBAN_DESTINO) references cuentabancaria(IBAN) on delete cascade
);