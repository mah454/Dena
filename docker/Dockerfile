FROM debian:latest 

ENV JAVA_HOME=/opt/jre
ENV PATH="$JAVA_HOME/bin:$PATH"

COPY jre /opt/jre
COPY dena /opt/dena

CMD ["/opt/dena/dena-runner"]
