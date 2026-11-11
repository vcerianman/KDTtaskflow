-- V2: Dữ liệu mẫu để Kids có sẵn cái mà GET/UPDATE/DELETE.

INSERT INTO project (name, description, status, owner, start_date, due_date) VALUES
                                                                                 ('B2B Customer Portal',    'Cổng khách hàng doanh nghiệp trên Next.js + BFF', 'ACTIVE',    'KDT',   '2026-03-01', '2026-09-30'),
                                                                                 ('HRM-CTEL',               'Hệ thống HRM nội bộ',                             'ACTIVE',    'KDT',   '2026-01-15', '2026-12-31'),
                                                                                 ('TaskFlow Capstone',      'Dự án tốt nghiệp của CTEL Kids',                  'PLANNING',  'Khanig',  '2026-07-01', '2026-09-20'),
                                                                                 ('CDR Reconciliation',     'Đối soát dữ liệu cước',                           'ON_HOLD',   'Khanig',  NULL,         NULL),
                                                                                 ('Legacy Billing Migrate', 'Di trú hệ thống billing cũ',                      'COMPLETED', 'NgoDo',   '2025-06-01', '2025-12-15');