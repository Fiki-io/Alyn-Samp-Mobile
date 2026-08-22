project(Alyn_SAMPMOBILE)

set(CMAKE_CXX_STANDARD 20)

if (${ANDROID_ABI} STREQUAL "armeabi-v7a")
    add_definitions(-DVER_x32=true)
elseif (${ANDROID_ABI} STREQUAL "arm64-v8a")
    add_definitions(-DVER_x32=false)
endif ()

include_directories(
    ${CMAKE_CURRENT_SOURCE_DIR}
    ${CMAKE_CURRENT_SOURCE_DIR}/Deps/spdlog/include
    ${CMAKE_CURRENT_SOURCE_DIR}/Deps/Opus/include
)

file(GLOB INCLUDE_FILES
        *.h
        Game/*.h
        Game/**/*.h
        Java/*.h
        Net/*.h
        UI/*.h
        UI/**/*.h
        UI/**/**/*.h
        Voice/*.h
        Voice/**/*.h
        Voice/**/**/*.h
)

file(GLOB SOURCE_FILES
        *.cpp
        Game/*.cpp
        Game/**/*.cpp
        Java/*.cpp
        Net/*.cpp
        UI/*.cpp
        UI/**/*.cpp
        UI/**/**/*.cpp
        Voice/*.cpp
        Voice/**/*.cpp
        Voice/**/**/*.cpp
)

add_library(${PROJECT_NAME} SHARED
        ${INCLUDE_FILES}
        ${SOURCE_FILES})

add_subdirectory(Memory)
add_subdirectory(Deps)
add_subdirectory(GameSA)

target_link_libraries(${PROJECT_NAME} PRIVATE
        android
        log
        EGL
        GLESv2
        Memory
        GameSA
        imgui
        rapidjson
        RakNet
        Encoding
        opus
        obfuscator
        bass
        spdlog)

target_compile_options(${PROJECT_NAME} PRIVATE
        -w # Disable warnings
        -O3 # Optimize
        -fexceptions # Enable exceptions
        -fstack-protector # Stack protector
#        -fvisibility=hidden # Hide symbols
#        -fvisibility-inlines-hidden # Hide inline symbols
)

target_compile_definitions(${PROJECT_NAME} PRIVATE RAKSAMP_CLIENT)