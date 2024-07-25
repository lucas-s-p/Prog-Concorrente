from sum import do_sum
import threading
import sys

def sum_run():
    def program_sun():
        paths = sys.argv[1:]
        for path in paths:
            _sum = do_sum(path)
            print(path + " : " + str(_sum), " ", thread.name)

    thread = threading.Thread(target=program_sun, name="thread_sum")
    thread.start()

if __name__ == '__main__':
    sum_run()
