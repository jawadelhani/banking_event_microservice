FROM jenkins/jenkins:lts

USER root

# Install Maven
RUN apt-get update && \
    apt-get install -y maven curl

# Install Docker CLI
RUN curl -fsSL https://download.docker.com/linux/static/stable/x86_64/docker-28.3.2.tgz \
    | tar -xz --strip-components=1 -C /usr/local/bin docker/docker

USER jenkins