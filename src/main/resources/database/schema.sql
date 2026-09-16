
create table if not exists tb_cliente (
	id bigint,
	nome varchar(50) not null,
	cpf bigint not null,
	tel bigint not null,
	endereco varchar(50) not null,
	numero bigint not null,
	cidade varchar(50) not null,
	estado varchar(50) not null,
 ativo boolean not null default true,
 constraint uk_cpf_cliente unique(cpf),
	constraint pk_id_cliente primary key(id)
);


create table if not exists tb_produto(
	id bigint,
	codigo varchar(10) not null,
	nome varchar(50) not null,
	descricao varchar(100) not null,
	valor numeric(10,2) not null,
	cupom_15_off boolean not null default false,
 constraint uk_codigo_produto unique(codigo),
 constraint pk_id_produto primary key(id)
);

create table if not exists tb_venda(
	id bigint,
	codigo varchar(10) not null,
	id_cliente_fk bigint not null,
	valor_total numeric(10,2) not null,
	data_venda TIMESTAMPTZ not null,
	status_venda varchar(50) not null,
	constraint uk_codigo_venda unique(codigo),
    constraint pk_id_venda primary key(id),
	constraint fk_id_cliente_venda foreign key(id_cliente_fk) references tb_cliente(id)
);

create table if not exists tb_produto_quantidade(
	id bigint,
	id_produto_fk bigint not null,
	id_venda_fk bigint not null,
	quantidade int not null,
	valor_total numeric(10,2) not null,
	constraint pk_id_prod_venda primary key(id),
	constraint fk_id_prod_venda foreign key(id_produto_fk) references tb_produto(id),
	constraint fk_id_prod_venda_venda foreign key(id_venda_fk) references tb_venda(id)
);

create sequence if not exists sq_cliente
start 1
increment 1
owned by tb_cliente.id;

create sequence if not exists sq_produto
start 1
increment 1
owned by tb_produto.id;

create sequence if not exists sq_venda
start 1
increment 1
owned by tb_venda.id;

create sequence if not exists sq_produto_quantidade
start 1
increment 1
owned by tb_produto_quantidade.id;


create table if not exists tb_estoque (
 id bigint,
 id_produto_fk bigint not null,
 quantidade int not null default 0,
 constraint pk_id_estoque primary key(id),
 constraint uk_produto_estoque unique(id_produto_fk),
 constraint fk_produto_estoque foreign key(id_produto_fk) references tb_produto(id) on delete cascade,
 constraint ck_quantidade_estoque check(quantidade >= 0)
);
create sequence if not exists sq_estoque start 1 increment 1 owned by tb_estoque.id;
