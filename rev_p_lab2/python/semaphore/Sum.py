import os
import sys
import threading

# Variável global para a soma total e o semáforo para proteger a região crítica
sum_total = 0
mutex = threading.Semaphore(1)

def do_sum(path):
    _sum = 0
    with open(path, 'rb', buffering=0) as f:
        byte = f.read(1)
        while byte:
            _sum += int.from_bytes(byte, byteorder='big', signed=False)
            byte = f.read(1)
    return _sum
    
def sum_file(path):
    global sum_total
    _sum = do_sum(path)
    # Região crítica
    mutex.acquire()
    try:
        sum_total += _sum
    finally:
        mutex.release()
    print(path + " : " + str(_sum) + " " + threading.current_thread().name)

if __name__ == "__main__":
    paths = sys.argv[1:]
    threads = []

    for path in paths:
        thread = threading.Thread(target=sum_file, args=(path,), name="my_thread_lucas")
        threads.append(thread)
        thread.start()

    for thread in threads:
        thread.join()

    print("A soma total é igual: " + str(sum_total))
