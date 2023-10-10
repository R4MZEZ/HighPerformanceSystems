alter table user_interactions
    alter column is_liked set not null;

alter table user_interactions
    alter column is_liked set default false;