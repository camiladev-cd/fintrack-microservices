CREATE TABLE payments (
                          id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          user_id           UUID NOT NULL,
                          payment_intent_id VARCHAR(255) NOT NULL UNIQUE,
                          idempotency_key   VARCHAR(255) NOT NULL UNIQUE,
                          amount            BIGINT NOT NULL,
                          currency          VARCHAR(3) NOT NULL,
                          status            VARCHAR(50) NOT NULL,
                          description       TEXT,
                          failure_reason    TEXT,
                          created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                          updated_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_payments_user_id ON payments(user_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_created_at ON payments(created_at DESC);