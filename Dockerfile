FROM maven:3-jdk-8-alpine

ARG PR_PROJECT_BOOT_NAME="${PR_PROJECT_BOOT_NAME}"
ARG PR_PROJECT_TARGET_NAME="${PR_PROJECT_TARGET_NAME}"
ARG PR_PROJECT_PORT="${PR_PROJECT_PORT}"
ENV PRE_PROJECT_TARGET_NAME="${PR_PROJECT_TARGET_NAME}"

ENV TZ=Asia/Seoul

WORKDIR /usr/src/app

COPY ./${PR_PROJECT_BOOT_NAME}/target/${PR_PROJECT_TARGET_NAME} /usr/src/app

EXPOSE ${PR_PROJECT_PORT}

CMD java -jar "${PRE_PROJECT_TARGET_NAME}"
