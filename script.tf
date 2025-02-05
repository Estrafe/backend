provider "aws" {
  region = "eu-south-2"  # Change to your preferred AWS region
}

### 🔹 S3 Bucket for storing PDFs & assets
resource "aws_s3_bucket" "estrafe_storage" {
  bucket = "estrafe-tickets-storage"
}

### 🔹 API Gateway for Exposing Endpoints
resource "aws_api_gateway_rest_api" "estrafe_api" {
  name        = "EstrafeAPI"
  description = "REST API for ticket booking system"
}

### 🔹 Lambda Function for PDF Generation
resource "aws_lambda_function" "generate_pdf" {
  function_name    = "GeneratePDF"
  role            = aws_iam_role.lambda_role.arn
  handler         = "index.handler"
  runtime         = "nodejs18.x"
  filename        = "lambda_function.zip"  # Upload your ZIP file manually or use Terraform to package it
  source_code_hash = filebase64sha256("lambda_function.zip")
}

### 🔹 IAM Role for Lambda
resource "aws_iam_role" "lambda_role" {
  name = "lambda_execution_role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Principal = {
        Service = "lambda.amazonaws.com"
      }
    }]
  })
}

### 🔹 ECS Fargate for Spring Boot Backend
resource "aws_ecs_cluster" "estrafe_cluster" {
  name = "estrafe-cluster"
}

resource "aws_ecs_task_definition" "estrafe_backend" {
  family                   = "estrafe-backend"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "512"
  memory                   = "1024"

  container_definitions = jsonencode([{
    name  = "estrafe-backend"
    image = "your-docker-image-url"  # Replace with ECR image URL
    portMappings = [{
      containerPort = 8080
      hostPort      = 8080
    }]
    environment = [
      { name = "DB_HOST", value = aws_db_instance.estrafe_db.endpoint },
      { name = "DB_USER", value = "admin" },
      { name = "DB_PASSWORD", value = "your-password" }
    ]
  }])
}

resource "aws_ecs_service" "estrafe_backend_service" {
  name            = "estrafe-backend-service"
  cluster        = aws_ecs_cluster.estrafe_cluster.id
  task_definition = aws_ecs_task_definition.estrafe_backend.arn
  launch_type     = "FARGATE"
  network_configuration {
    subnets = aws_subnet.public.*.id
    security_groups = [aws_security_group.estrafe_sg.id]
  }
}

### 🔹 MySQL RDS Database for Ticketing Data
resource "aws_db_instance" "estrafe_db" {
  allocated_storage    = 20
  engine              = "mysql"
  instance_class      = "db.t3.micro"
  db_name             = "estrafeDB"
  username           = "admin"
  password           = "your-password"
  parameter_group_name = "default.mysql8.0"
  publicly_accessible = false
  multi_az           = true  # High availability
}

### 🔹 CloudFront CDN for Frontend Hosting (React & Angular)
resource "aws_cloudfront_distribution" "estrafe_frontend" {
  origin {
    domain_name = aws_s3_bucket.estrafe_storage.bucket_regional_domain_name
    origin_id   = "S3-estrafe"
  }

  enabled         = true
  default_root_object = "index.html"

  default_cache_behavior {
    allowed_methods = ["GET", "HEAD", "OPTIONS"]
    cached_methods  = ["GET", "HEAD"]
    target_origin_id = "S3-estrafe"

    viewer_protocol_policy = "redirect-to-https"
  }

  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }

  viewer_certificate {
    cloudfront_default_certificate = true
  }
}

### 🔹 Security Group for Backend
resource "aws_security_group" "estrafe_sg" {
  name        = "estrafe_security_group"
  description = "Allow inbound traffic for API & Database"

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 3306
    to_port     = 3306
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

### 🔹 Outputs
output "api_gateway_url" {
  value = aws_apigatewayv2_api.estrafe_api.api_endpoint
}

output "s3_bucket_name" {
  value = aws_s3_bucket.estrafe_storage.bucket
}

output "cloudfront_url" {
  value = aws_cloudfront_distribution.estrafe_frontend.domain_name
}