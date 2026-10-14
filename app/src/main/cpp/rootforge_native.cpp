#include <jni.h>
#include <sys/utsname.h>
#include <unistd.h>
#include <fstream>
#include <sstream>
#include <string>

namespace {
std::string read_line(const char* path) {
    std::ifstream stream(path);
    std::string line;
    return (stream && std::getline(stream, line)) ? line : "unavailable";
}
std::string inspect() {
    struct utsname info {};
    std::ostringstream out;
    out << "RootForge native diagnostic backend v0.4\n";
    if (uname(&info) == 0) {
        out << "Kernel: " << info.release << "\n";
        out << "Machine: " << info.machine << "\n";
    } else {
        out << "Kernel: unavailable\n";
    }
    out << "Process UID: " << getuid() << "\n";
    out << "Process EUID: " << geteuid() << "\n";
    out << "SELinux enforce flag: " << read_line("/sys/fs/selinux/enforce") << "\n";
    out << "Read-only inspection; no root escalation or writes.";
    return out.str();
}
}
extern "C" JNIEXPORT jstring JNICALL
Java_com_sysopbp_rootforgesu_NativeCore_inspect(JNIEnv* env, jobject) {
    const std::string output = inspect();
    return env->NewStringUTF(output.c_str());
}
