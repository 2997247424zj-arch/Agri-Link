# Local business testing accounts

`dev-login-accounts.sql` prepares a safe, repeatable set of test accounts and interactive records in a local/demo MySQL `rongxiaotong` database.

> Do NOT execute this script on production data.

## Login information

All accounts use the same plain password: `Test@123456`

| Account | Role | Select in frontend | Useful verification pages |
| --- | --- | --- | --- |
| `dev_admin` | System admin | System admin | Admin users, trade orders, purchases, finance, knowledge |
| `dev_farmer` | Farmer | Farmer | Trade publishing, approved/pending finance applications, expert consultation |
| `dev_farmer2` | Farmer | Farmer | Orchard products, rejected/pending finance cases, fruit-tree reservations |
| `dev_buyer` | Buyer | Buyer | Trade browsing, cart, pending purchase orders, knowledge comments |
| `dev_buyer2` | Buyer | Buyer | Supermarket bulk purchases, delivered orders, logistics and comments |
| `dev_expert` | Expert | Expert | Fruit-tree Q&A replies, onsite reservations, knowledge content |
| `dev_expert2` | Expert | Expert | Crop-protection Q&A, remote reservations, rice guidance content |
| `dev_bank` | Bank | Bank | Spring-farming products, finance approval and farmer matching |
| `dev_bank2` | Bank | Bank | Orchard/equipment products, rejected cases and intention matching |

## Seed data scope

The script uses fixed IDs from `19001` to `19040` and cleans the same range before inserting, so it can be executed repeatedly.

Tables covered:

- `tb_user`: nine accounts across five roles;
- `tb_expert`: two expert profiles;
- `tb_bank` / `tb_bank_user`: four products and two bank users;
- `tb_address`: buyer/farmer addresses;
- `tb_order`: eight Chinese-named products and services from two farmers;
- `tb_shoppingcart`: carts for both buyers;
- `tb_purchase` / `tb_purchase_detail` / `tb_sell_purchase`: purchase flow records;
- `tb_finance` / `tb_financing_intention`: pending, approved and rejected cases;
- `tb_question` / `tb_reserve`: answered/pending Q&A and onsite/video reservations;
- `tb_knowledge` / `tb_discuss`: Chinese guidance articles, platform news and comments.

## How to execute

Open a MySQL client connected to your local `rongxiaotong` database and run:

```sql
SOURCE docs/dev-login-accounts.sql;
```

Or open `docs/dev-login-accounts.sql` in a GUI database tool and execute the whole script.

## Security notes

- Stored passwords are BCrypt hashes, not plain text;
- `Test@123456` is only for local business testing;
- To remove the data, delete `dev_%` users and records with IDs in `19001-19020`, or simply re-run the script to reset them.
