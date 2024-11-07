# use the latest stable openjdk 17 LTS base image
FROM openjdk:17.0.2-jdk-slim

# install necessary dependencies, including gradle
RUN apt-get update && \
    apt-get install -y curl unzip wget && \
    rm -rf /var/lib/apt/lists/*

# install the latest stable gradle version (8.10.2 as of nov 2024)
ARG GRADLE_VERSION=8.10.2
RUN wget https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip && \
    unzip gradle-${GRADLE_VERSION}-bin.zip -d /opt && \
    rm gradle-${GRADLE_VERSION}-bin.zip && \
    ln -s /opt/gradle-${GRADLE_VERSION}/bin/gradle /usr/bin/gradle

# set the working directory
WORKDIR /app

# copy all project files into the container
COPY app /app

# generate the gradle wrapper within the container
RUN gradle wrapper

# ensure the gradle wrapper script is executable
RUN chmod +x ./gradlew

# run the gradle build to compile the kotlin project
RUN ./gradlew build || { echo "gradle build failed"; exit 1; }

# expose the application port
EXPOSE 8080

# set the entry point to run the application and keep the container running
CMD sh -c "./gradlew run && tail -f /dev/null"
