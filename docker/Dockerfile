FROM debian:latest 

ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8
ENV JAVA_HOME=/opt/jre
ENV PATH="$JAVA_HOME/bin:$PATH"

COPY jre /opt/jre
COPY dena /opt/dena

RUN ln -s /opt/dena /dena

CMD ["/opt/dena/dena-runner"]

EXPOSE 2120 5005
