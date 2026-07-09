CREATE DATABASE fintrack_auth;
CREATE DATABASE fintrack_users;
CREATE DATABASE fintrack_wallets;
CREATE DATABASE fintrack_transactions;

GRANT ALL PRIVILEGES ON DATABASE fintrack_auth TO fintrack_user;
GRANT ALL PRIVILEGES ON DATABASE fintrack_users TO fintrack_user;
GRANT ALL PRIVILEGES ON DATABASE fintrack_wallets TO fintrack_user;
GRANT ALL PRIVILEGES ON DATABASE fintrack_transactions TO fintrack_user;


CREATE DATABASE fintrack_payments;
GRANT ALL PRIVILEGES ON DATABASE fintrack_payments TO fintrack_user;