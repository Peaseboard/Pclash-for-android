cmake_minimum_required(VERSION 3.0)
project(mihomo_bridge)

set(CMAKE_C_STANDARD 11)
set(CMAKE_CXX_STANDARD 11)

include_directories(${CLASH_OUTPUT}/${ANDROID_ABI} ${CLASH_SOURCE} ${CLASH_SOURCE}/../golang)
link_directories(${CLASH_OUTPUT}/${CMAKE_ANDROID_ARCH_ABI})
link_libraries(log mihomo)

add_library(bridge SHARED main.cpp main.h init.cpp query.cpp patch.cpp tun.cpp defer.cpp log.cpp event_queue.cpp)