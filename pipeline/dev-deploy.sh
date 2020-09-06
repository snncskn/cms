#!/usr/bin/env sh
set -e

ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@ssh.mine-tec.co.za <<ENDSSH
set -e
curl -s --fail -X POST http://localhost:9093/actuator/shutdown && echo "SHUTDOWN SUCCEEDED" || echo "SHUTDOWN FAILED"
rm -rf /opt/cms/dev/cms-backend.jar
rm -rf /opt/cms/dev/frontend
ENDSSH

scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no backend/target/cms-backend.jar root@ssh.mine-tec.co.za:/opt/cms/dev

scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -r frontend/dist root@ssh.mine-tec.co.za:/opt/cms/dev

ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@ssh.mine-tec.co.za <<ENDSSH
set -e
source '/root/.sdkman/bin/sdkman-init.sh'
cd /opt/cms/dev
nohup java -Dspring.config.location=file:application.properties -jar cms-backend.jar --logging.config=./logback.xml > /dev/null 2>&1 &

mv dist frontend
ENDSSH
