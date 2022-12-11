Create table Cliente(
DNI varchar(50) primary key,
Nombre varchar(50),
Apellido varchar(50),
Edad integer,
Email varchar(50),
contraseña varchar(250)
);

Create table CuentaBancaria(
IBAN varchar(50) primary key,
dinero double,
DNI_cliente varchar(50),
foreign key (DNI_cliente) references cliente(DNI)
);

Create table Transferencia(
IBAN_ORIGEN varchar(50),
IBAN_DESTINO varchar(50),
dinero double,
foreign key (IBAN_ORIGEN) references cuentabancaria(IBAN),
foreign key (IBAN_DESTINO) references cuentabancaria(IBAN)
);