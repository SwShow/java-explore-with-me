drop table if exists users cascade;
drop table if exists categories cascade;
drop table if exists events cascade;
drop table if exists requests cascade;
drop table if exists compilations cascade;
drop table if exists compilation_ev cascade;

create table if not exists users
(
    id
    bigint
    generated
    by
    default as
    identity
    not
    null,
    name
    varchar
(
    50
),
    email varchar,
    constraint pk_user primary key
(
    id
)
    );

create table if not exists categories
(
    id
    bigint
    generated
    by
    default as
    identity
    not
    null,
    name
    varchar
(
    50
) not null unique,
    constraint pk_categories primary key
(
    id
)
    );

create table if not exists events
(
    id
    bigint
    generated
    by
    default as
    identity
    not
    null,
    annotation
    varchar
(
    1000
),
    category_id bigint not null references categories
(
    id
) on delete cascade,
    confirmed_requests bigint,
    created_on timestamp
  without time zone not null,
    description varchar
(
    1000
),
    event_date timestamp
  without time zone not null,
    initiator_id bigint not null references users
(
    id
)
  on delete cascade,
    lat float not null,
    lon float not null,
    paid boolean not null,
    participant_limit bigint,
    published_on timestamp
  without time zone,
    request_moderation boolean not null,
    state varchar not null,
    title varchar
(
    1000
),
    views bigint default 0,
    constraint pk_events primary key
(
    id
)
    );

create table if not exists requests
(
    id
    bigint
    generated
    by
    default as
    identity
    not
    null,
    created
    TIMESTAMP
    WITHOUT
    TIME
    ZONE
    NOT
    NULL,
    event_id
    bigint
    not
    null
    references
    events
(
    id
) on delete cascade,
    requester_id bigint not null references users
(
    id
)
  on delete cascade,
    status varchar not null,
    constraint pk_requests primary key
(
    id
)
    );

create table if not exists compilations
(
    id
    bigint
    generated
    by
    default as
    identity
    not
    null,
    pinned
    boolean
    not
    null,
    title
    varchar
(
    100
),
    event_id bigint references events
(
    id
) on delete cascade,
    constraint pk_compilations primary key
(
    id
)
    );

create table if not exists compilation_ev
(
    event_id
    bigint,
    compilation_id
    bigint,
    constraint
    pk_ev
    FOREIGN
    key
(
    event_id
) references events
(
    id
),
    constraint pk_comp foreign key
(
    compilation_id
) references compilations
(
    id
),
    primary key
(
    event_id,
    compilation_id
)
    );

