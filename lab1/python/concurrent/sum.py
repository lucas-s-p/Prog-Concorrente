import os
import sys
import threading

def do_sum(path):
    _sum = 0
    with open(path, 'rb',buffering=0) as f:
        byte = f.read(1)
        while byte:
            _sum += int.from_bytes(byte, byteorder='big', signed=False)
            byte = f.read(1)
        return _sum

def sum_file(path):
    _sum = do_sum(path)
    print(path + " : " + str(_sum))

if __name__ == "__main__":
    paths = sys.argv[1:]
    for path in paths:
        thread = threading.Thread(target=sum_file(path))
        thread.start()
