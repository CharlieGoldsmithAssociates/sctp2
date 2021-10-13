alter table households
    add labor_constrained boolean,
    add dependency_ratio int;

alter table ubr_csv_imports
    add labor_constrained boolean,
    add dependency_ratio int;