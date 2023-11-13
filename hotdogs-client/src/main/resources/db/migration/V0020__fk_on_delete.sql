ALTER TABLE dogs DROP CONSTRAINT "dogs_breed_fkey";
ALTER TABLE dogs ADD CONSTRAINT "dogs_breed_fkey" FOREIGN KEY (cur_recommended) REFERENCES dogs(id) ON DELETE SET NULL;
