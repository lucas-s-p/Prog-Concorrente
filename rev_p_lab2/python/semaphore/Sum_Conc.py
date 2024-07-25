import os
import sys
import threading

# Variável global para a soma total e o semáforo para proteger a região crítica
sum_total = 0
mutex = threading.Semaphore(1)

def do_sum(path, start, end):
    _sum = 0
    with open(path, 'rb') as f:
        f.seek(start)
        for _ in range(start, end):
            byte = f.read(1)
            if not byte:
                break
            _sum += int.from_bytes(byte, byteorder='big', signed=False)
    return _sum
    
def sum_file(path, start, end):
    global sum_total
    _sum = do_sum(path, start, end)
    # Região crítica
    sum_total += _sum
    print(path + f" [{start}, {end}] : " + str(_sum) + " " + threading.current_thread().name)

if __name__ == "__main__":
    paths = sys.argv[1:]
    threads = []

    for path in paths:
        file_size = os.path.getsize(path)
        chunk_size = file_size // 4
        for i in range(4):
            start = i * chunk_size
            end = file_size if i == 3 else (i + 1) * chunk_size
            thread = threading.Thread(target=sum_file, args=(path, start, end), name="my_thread_lucas")
            threads.append(thread)
            thread.start()

    for thread in threads:
        thread.join()

    print("A soma total é igual: " + str(sum_total))

