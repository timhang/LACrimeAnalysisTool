import argparse
import mysql.connector
import csv

# Database setup function
def setup_database():
    db_config = {
        'db1': {'host': '{url}', 'port': 3306, 'database': 'CrimeData', 'user': 'root', 'password': 'crimedata1'},
        'db2': {'host': '{url}', 'port': 3306, 'database': 'CrimeData', 'user': 'root', 'password': 'crimedata2'},
        'db3': {'host': '{url}', 'port': 3306, 'database': 'CrimeData', 'user': 'root', 'password': 'crimedata3'}
    }
    # conn = sqlite3.connect('crime_data.db')
    # conn = mysql.connector.connect(**db_config.get('db1'))
    connections = {}
    for key in db_config:
        connections[key] = mysql.connector.connect(**db_config[key])
    # cursor = conn.cursor()
    # cursor.execute('''
    # CREATE TABLE IF NOT EXISTS CrimeData (
    #     DR_NO VARCHAR(20) PRIMARY KEY,
    #     Date_Rptd DATETIME,
    #     DATE_OCC DATETIME,
    #     TIME_OCC INT,
    #     AREA INT,
    #     AREA_NAME TEXT,
    #     Rpt_Dist_No TEXT,
    #     Crm_Cd TEXT,
    #     Crm_Cd_Desc TEXT,
    #     Vict_Age INT,
    #     Vict_Sex TEXT,
    #     Vict_Descent TEXT,
    #     Premis_Cd INT,
    #     Premis_Desc TEXT,
    #     Weapon_Used_Cd TEXT,
    #     Weapon_Desc TEXT,
    #     Status TEXT,
    #     Status_Desc TEXT,
    #     LOCATION TEXT,
    #     Cross_Street TEXT,
    #     LAT FLOAT(10,6),
    #     LON FLOAT(10,6)
    # )
    # ''')
    # conn.commit()
    return connections

def get_connection(connections, dr_no):
    index = int(dr_no) % 3
    db_key = 'db' + str(index + 1)
    return connections[db_key]

# Function to handle individual insert
def insert_data(connections, args):
    conn = get_connection(connections, args.DR_NO)
    with conn:
        cursor = conn.cursor()
        cursor.execute('''
        INSERT INTO CrimeData (
            DR_NO, Date_Rptd, DATE_OCC, TIME_OCC, AREA, AREA_NAME, Rpt_Dist_No, Crm_Cd, Crm_Cd_Desc,
            Vict_Age, Vict_Sex, Vict_Descent, Premis_Cd, Premis_Desc, Weapon_Used_Cd, Weapon_Desc,
            Status, Status_Desc, LOCATION, Cross_Street, LAT, LON
        ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        ''', (
            args.DR_NO, args.Date_Rptd, args.DATE_OCC, args.TIME_OCC, args.AREA, args.AREA_NAME,
            args.Rpt_Dist_No, args.Crm_Cd, args.Crm_Cd_Desc, args.Vict_Age, args.Vict_Sex,
            args.Vict_Descent, args.Premis_Cd, args.Premis_Desc, args.Weapon_Used_Cd,
            args.Weapon_Desc, args.Status, args.Status_Desc, args.LOCATION, args.Cross_Street,
            args.LAT, args.LON
        ))
        conn.commit()
        print("Record inserted successfully into db", int(args.DR_NO) % 3 + 1)

# Function to handle individual update
def update_data(connections, args):
    # Get the right database connection based on hashed DR_NO
    conn = get_connection(connections, args.DR_NO)

    # Prepare SQL set clause and values
    fields = [f"{k} = %s" for k, v in vars(args).items() if v is not None and k != 'DR_NO' and k != 'action']
    sql_set = ', '.join(fields)
    values = [v for k, v in vars(args).items() if v is not None and k != 'DR_NO' and k != 'action']
    values.append(args.DR_NO)

    # Execute the update
    with conn:
        cursor = conn.cursor()
        cursor.execute(f"UPDATE CrimeData SET {sql_set} WHERE DR_NO = %s", values)
        conn.commit()
        print("Record updated successfully in db", int(args.DR_NO) % 3 + 1)

# Function to handle individual delete
def delete_data(connections, args):
    # Get the right database connection based on hashed DR_NO
    conn = get_connection(connections, args.DR_NO)
    
    # Execute the delete operation
    with conn:
        cursor = conn.cursor()
        try:
            cursor.execute("DELETE FROM CrimeData WHERE DR_NO = %s", (args.DR_NO,))
            conn.commit()
            print("Record deleted successfully from db", int(args.DR_NO) % 3 + 1)
        except mysql.connector.Error as err:
            print(f"Error in deleting record: {err}")

# Function to fetch and display all data from the table
def fetch_all_data(connections):
    # cursor = conn.cursor()
    # cursor.execute("SELECT * FROM CrimeData LIMIT 100")
    # records = cursor.fetchall()
    # for record in records:
    #     print(record)
    all_records = []
    for db_key, conn in connections.items():
        with conn:
            cursor = conn.cursor()
            cursor.execute("SELECT * FROM CrimeData ORDER BY DR_NO LIMIT 33")
            records = cursor.fetchall()
            all_records.extend(records)
            print(f"Fetched {len(records)} records from {db_key}.")

    # Sort all records by DR_NO which is assumed to be the first column
    all_records.sort(key=lambda record: record[0])  # Adjust the index if DR_NO is not the first column

    # Optionally, print all records or handle them as needed
    for record in all_records:
        print(record)

def get_connection_key(dr_no):
    index = int(dr_no) % 3  # Hash DR_NO by taking the modulo of 3
    return 'db' + str(index + 1)

# Function to handle bulk insert from CSV
def bulk_insert_from_csv(connections, csv_file_path):
    # with open(csv_file_path, 'r') as file:
    #     reader = csv.DictReader(file)
    #     placeholders = ', '.join('?' * len(reader.fieldnames))
    #     sql = f"INSERT INTO CrimeData ({', '.join(reader.fieldnames)}) VALUES ({placeholders})"
    #     with conn:
    #         conn.executemany(sql, (tuple(row.values()) for row in reader))
    #         conn.commit()
    #     print("Bulk insert completed successfully.")
    # Prepare to categorize records by target database
    database_buckets = {'db1': [], 'db2': [], 'db3': []}

    # Read from CSV and distribute records
    with open(csv_file_path, 'r') as file:
        reader = csv.DictReader(file)
        for row in reader:
            dr_no = row['DR_NO']
            db_key = get_connection_key(dr_no)
            database_buckets[db_key].append(tuple(row.values()))

    # Perform the inserts
    for db_key, records in database_buckets.items():
        if records:  # Check if there are any records to insert
            conn = connections[db_key]
            with conn:
                cursor = conn.cursor()
                placeholders = ', '.join(['%s'] * len(records[0]))
                columns = ', '.join(reader.fieldnames)
                sql = f"INSERT INTO CrimeData ({columns}) VALUES ({placeholders})"
                cursor.executemany(sql, records)
                conn.commit()
                print(f"Bulk insert completed successfully on {db_key} with {len(records)} records.")

# Function to handle bulk update with SQL expression
def bulk_update(connections, sql_expression):
    for key, conn in connections.items():
        with conn:
            cursor = conn.cursor()
            try:
                cursor.execute(f"UPDATE CrimeData SET {sql_expression}")
                conn.commit()
                print(f"Bulk update completed successfully on {key}.")
            except mysql.connector.Error as err:
                print(f"Error in updating records in {key}: {err}")

# Function to handle bulk delete with a condition
def bulk_delete(connections, condition):
    for key, conn in connections.items():
        with conn:
            cursor = conn.cursor()
            try:
                cursor.execute(f"DELETE FROM CrimeData WHERE {condition}")
                conn.commit()
                print(f"Bulk delete completed successfully on {key}.")
            except mysql.connector.Error as err:
                print(f"Error in deleting records from {key}: {err}")



# Argument parsing function
def parse_args():
    parser = argparse.ArgumentParser(description='Database Management CLI for CrimeData')
    subparsers = parser.add_subparsers(dest='action', required=True)

    # Parser for individual insert
    insert_parser = subparsers.add_parser('insert')
    insert_parser.add_argument('DR_NO', type=str)
    insert_parser.add_argument('Date_Rptd', type=str)
    insert_parser.add_argument('DATE_OCC', type=str)
    insert_parser.add_argument('TIME_OCC', type=int)
    insert_parser.add_argument('AREA', type=int)
    insert_parser.add_argument('AREA_NAME', type=str)
    insert_parser.add_argument('Rpt_Dist_No', type=str)
    insert_parser.add_argument('Crm_Cd', type=str)
    insert_parser.add_argument('Crm_Cd_Desc', type=str)
    insert_parser.add_argument('Vict_Age', type=int)
    insert_parser.add_argument('Vict_Sex', type=str)
    insert_parser.add_argument('Vict_Descent', type=str)
    insert_parser.add_argument('Premis_Cd', type=int)
    insert_parser.add_argument('Premis_Desc', type=str)
    insert_parser.add_argument('Weapon_Used_Cd', type=str, nargs='?')
    insert_parser.add_argument('Weapon_Desc', type=str, nargs='?')
    insert_parser.add_argument('Status', type=str)
    insert_parser.add_argument('Status_Desc', type=str)
    insert_parser.add_argument('LOCATION', type=str)
    insert_parser.add_argument('Cross_Street', type=str, nargs='?')
    insert_parser.add_argument('LAT', type=float)
    insert_parser.add_argument('LON', type=float)

    # Parser for individual update
    update_parser = subparsers.add_parser('update')
    update_parser.add_argument('DR_NO', type=str)
    # Optional arguments for fields that can be updated
    for field in ['Date_Rptd', 'DATE_OCC', 'TIME_OCC', 'AREA', 'AREA_NAME', 'Rpt_Dist_No', 'Crm_Cd', 'Crm_Cd_Desc', 'Vict_Age', 'Vict_Sex', 'Vict_Descent', 'Premis_Cd', 'Premis_Desc', 'Weapon_Used_Cd', 'Weapon_Desc', 'Status', 'Status_Desc', 'LOCATION', 'Cross_Street', 'LAT', 'LON']:
        update_parser.add_argument(f'--{field}', type=str)

    # Parser for individual delete
    delete_parser = subparsers.add_parser('delete')
    delete_parser.add_argument('DR_NO', type=str)

    # Parser for viewing all records
    view_parser = subparsers.add_parser('view')

    # Parser for bulk insert from CSV
    bulk_insert_parser = subparsers.add_parser('bulk_insert')
    bulk_insert_parser.add_argument('csv_file_path', type=str)

    # Parser for bulk update
    bulk_update_parser = subparsers.add_parser('bulk_update')
    bulk_update_parser.add_argument('sql_expression', type=str)

    # Parser for bulk delete
    bulk_delete_parser = subparsers.add_parser('bulk_delete')
    bulk_delete_parser.add_argument('condition', type=str)

    return parser.parse_args()


def main():
    connections = setup_database()
    args = parse_args()

    if args.action == 'insert':
        insert_data(connections, args)
    elif args.action == 'update':
        update_data(connections, args)
    elif args.action == 'delete':
        delete_data(connections, args)
    elif args.action == 'view':
        fetch_all_data(connections)
    elif args.action == 'bulk_insert':
        bulk_insert_from_csv(connections, args.csv_file_path)
    elif args.action == 'bulk_update':
        bulk_update(connections, args.sql_expression)
    elif args.action == 'bulk_delete':
        bulk_delete(connections, args.condition)
    else:
        print("No valid action specified.")

    for conn in connections.values():
        conn.close()
    
    
    
if __name__ == '__main__':
   main()
