sed -i '/#include "selinux\/selinux.h"/a \
bool ksu_vfs_read_hook = true;\n\
bool ksu_execveat_hook = true;\n\
bool ksu_input_hook = true;\n\
EXPORT_SYMBOL(ksu_vfs_read_hook);\n\
EXPORT_SYMBOL(ksu_execveat_hook);\n\
EXPORT_SYMBOL(ksu_input_hook);' ./kernel/ksud.c

sed -i '/#include <linux\/types.h>/a \
extern bool ksu_vfs_read_hook;\n\
extern bool ksu_execveat_hook;\n\
extern bool ksu_input_hook;' ./kernel/ksud.h

