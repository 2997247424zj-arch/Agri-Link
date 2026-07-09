# Local business testing accounts

`dev-login-accounts.sql` prepares a safe, repeatable set of test accounts and interactive records in a local/demo MySQL `rongxiaotong` database.

> Do NOT execute this script on production data.

## Login information

All accounts use the same plain password: `Test@123456`

| Account | Role | Select in frontend | Useful verification pages |
| --- | --- | --- | --- |
| `dev_admin` | System admin | System admin | Admin users, trade orders, purchases, finance, knowledge |
| `dev_farmer` | Farmer | Farmer | Trade publishing, profile records, finance application, expert consultation |
| `dev_buyer` | Buyer | Buyer | Trade browsing, cart, purchase orders, knowledge comments |
| `dev_expert` | Expert | Expert | Expert profile, Q&A replies, reservation handling, knowledge content |
| `dev_bank` | Bank | Bank | Bank products, finance approval, farmer financing intentions |

## Seed data scope

The script uses fixed IDs from `19001` to `19020` and cleans the same range before inserting, so it can be executed repeatedly.

Tables covered:

- `tb_user`: five role accounts;
- `tb_expert`: expert profile;
- `tb_bank` / `tb_bank_user`: bank products and bank user;
- `tb_address`: buyer/farmer addresses;
- `tb_order`: farmer trade orders;
- `tb_shoppingcart`: buyer cart;
- `tb_purchase` / `tb_purchase_detail` / `tb_sell_purchase`: purchase flow records;
- `tb_finance` / `tb_financing_intention`: finance applications and intentions;
- `tb_question` / `tb_reserve`: expert Q&A and reservations;
- `tb_knowledge` / `tb_discuss`: knowledge articles and comments.

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
