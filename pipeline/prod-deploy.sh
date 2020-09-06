#!/usr/bin/env sh
set -e

ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@ssh.mine-tec.co.za <<ENDSSH
set -e
curl -s --fail -X POST http://localhost:9094/actuator/shutdown && echo "SHUTDOWN SUCCEEDED" || echo "SHUTDOWN FAILED"
rm -rf /opt/cms/opsicol/cms-backend.jar
rm -rf /opt/cms/opsicol/frontend
ENDSSH

scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no backend/target/cms-backend.jar root@ssh.mine-tec.co.za:/opt/cms/opsicol

scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -r frontend/dist root@ssh.mine-tec.co.za:/opt/cms/opsicol

ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@ssh.mine-tec.co.za <<ENDSSH
set -e
source '/root/.sdkman/bin/sdkman-init.sh'
cd /opt/cms/opsicol
nohup java -Dspring.config.location=file:application.properties -jar cms-backend.jar --logging.config=./logback.xml > /dev/null 2>&1 &

mv dist frontend

ENDSSH

