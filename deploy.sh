aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 932486566412.dkr.ecr.ap-northeast-2.amazonaws.com
docker build -t ssho-item-api .
docker tag ssho-item-api 932486566412.dkr.ecr.ap-northeast-2.amazonaws.com/ssho-item-api
docker push 932486566412.dkr.ecr.ap-northeast-2.amazonaws.com/ssho-item-api